import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FriendRequestDetailComponent } from './friend-request-detail.component';

describe('FriendRequest Management Detail Component', () => {
  let comp: FriendRequestDetailComponent;
  let fixture: ComponentFixture<FriendRequestDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FriendRequestDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ friendRequest: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(FriendRequestDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FriendRequestDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load friendRequest on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.friendRequest).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
