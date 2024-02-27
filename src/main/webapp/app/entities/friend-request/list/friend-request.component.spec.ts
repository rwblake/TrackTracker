import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { FriendRequestService } from '../service/friend-request.service';

import { FriendRequestComponent } from './friend-request.component';

describe('FriendRequest Management Component', () => {
  let comp: FriendRequestComponent;
  let fixture: ComponentFixture<FriendRequestComponent>;
  let service: FriendRequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'friend-request', component: FriendRequestComponent }]), HttpClientTestingModule],
      declarations: [FriendRequestComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(FriendRequestComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FriendRequestComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(FriendRequestService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.friendRequests?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to friendRequestService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getFriendRequestIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getFriendRequestIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
