import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { FriendshipService } from '../service/friendship.service';

import { FriendshipComponent } from './friendship.component';

describe('Friendship Management Component', () => {
  let comp: FriendshipComponent;
  let fixture: ComponentFixture<FriendshipComponent>;
  let service: FriendshipService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'friendship', component: FriendshipComponent }]), HttpClientTestingModule],
      declarations: [FriendshipComponent],
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
      .overrideTemplate(FriendshipComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FriendshipComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(FriendshipService);

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
    expect(comp.friendships?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to friendshipService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getFriendshipIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getFriendshipIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
