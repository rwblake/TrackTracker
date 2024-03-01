import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { StreamDetailComponent } from './stream-detail.component';

describe('Stream Management Detail Component', () => {
  let comp: StreamDetailComponent;
  let fixture: ComponentFixture<StreamDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StreamDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ stream: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(StreamDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(StreamDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load stream on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.stream).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
