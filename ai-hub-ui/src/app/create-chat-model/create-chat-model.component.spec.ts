import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateChatModelComponent } from './create-chat-model.component';

describe('CreateChatModelComponent', () => {
  let component: CreateChatModelComponent;
  let fixture: ComponentFixture<CreateChatModelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateChatModelComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CreateChatModelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
