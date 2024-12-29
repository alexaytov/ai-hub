package com.alexaytov.ai_hub.services.impl;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.alexaytov.ai_hub.config.ChromaDB;
import com.alexaytov.ai_hub.model.DataSourceProjection;
import com.alexaytov.ai_hub.model.dtos.DataDto;
import com.alexaytov.ai_hub.model.entities.DataSource;
import com.alexaytov.ai_hub.repositories.DataSourceRepository;
import com.alexaytov.ai_hub.services.DataSourceService;
import com.alexaytov.ai_hub.services.UserService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class DataSourceServiceImpl implements DataSourceService {

  private final DataSourceRepository dataSourceRepository;
  private final UserService userService;
  private final ChromaDB chromaDB;

  public DataSourceServiceImpl(
      DataSourceRepository dataSourceRepository, UserService userService, ChromaDB chromaDB) {
    this.dataSourceRepository = dataSourceRepository;
    this.userService = userService;
    this.chromaDB = chromaDB;
  }

  @Override
  public DataSource saveDataSource(DataDto dataSource) {
    DataSource source = new DataSource();
    source.setFileType(dataSource.getFileType());
    source.setFileName(dataSource.getFileName());
    source.setData(dataSource.getData());
    source.setUser(userService.getUser());

    // Make sure file name is unique
    if (dataSourceRepository
        .findByUserIdAndFileName(userService.getUser().getId(), dataSource.getFileName())
        .isPresent()) {
      throw new HttpClientErrorException(BAD_REQUEST, "File name already exists");
    }

    ChromaEmbeddingStore store =
        ChromaEmbeddingStore.builder()
            .collectionName("user_" + userService.getUser().getId())
            .baseUrl(chromaDB.getHost())
            .build();

    EmbeddingModel model = new AllMiniLmL6V2EmbeddingModel();

    EmbeddingStoreIngestor ingestor =
        EmbeddingStoreIngestor.builder()
                .embeddingModel(model).embeddingStore(store).build();

    String text;

    switch (dataSource.getFileType()) {
      case "application/pdf":
        try (PDDocument document = Loader.loadPDF(dataSource.getData())) {
          PDFTextStripper stripper = new PDFTextStripper();
          text = stripper.getText(document);
        } catch (IOException e) {
          throw new HttpClientErrorException(BAD_REQUEST, "Error reading PDF file");
        }
        break;
      case "text/plain":
        text = new String(dataSource.getData());
        break;
      default:
        throw new HttpClientErrorException(BAD_REQUEST, "Unsupported file type");
    }

    ingestor.ingest(Document.document(text, Metadata.metadata("name", dataSource.getFileName())));

    return dataSourceRepository.save(source);
  }

  @Override
  public List<DataSourceProjection> getDataSources() {
    return dataSourceRepository.findAllByUserId(userService.getUser().getId());
  }

  @Override
  @Transactional
  public void deleteDataSource(Long id) {
    dataSourceRepository.deleteByIdAndUserId(id, userService.getUser().getId());
  }
}
