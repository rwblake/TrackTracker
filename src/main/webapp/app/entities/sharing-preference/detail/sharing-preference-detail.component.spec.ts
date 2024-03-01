import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SharingPreferenceDetailComponent } from './sharing-preference-detail.component';

describe('SharingPreference Management Detail Component', () => {
  let comp: SharingPreferenceDetailComponent;
  let fixture: ComponentFixture<SharingPreferenceDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SharingPreferenceDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ sharingPreference: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(SharingPreferenceDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(SharingPreferenceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load sharingPreference on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.sharingPreference).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
