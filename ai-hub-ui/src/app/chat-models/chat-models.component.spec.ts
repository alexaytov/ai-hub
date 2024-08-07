import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChatModelsComponent } from './chat-models.component';

describe('ChatModelsComponent', () => {
  let component: ChatModelsComponent;
  let fixture: ComponentFixture<ChatModelsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChatModelsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ChatModelsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
