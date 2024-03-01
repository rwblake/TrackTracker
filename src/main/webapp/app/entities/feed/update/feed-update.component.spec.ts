import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FeedFormService } from './feed-form.service';
import { FeedService } from '../service/feed.service';
import { IFeed } from '../feed.model';

import { FeedUpdateComponent } from './feed-update.component';

describe('Feed Management Update Component', () => {
  let comp: FeedUpdateComponent;
  let fixture: ComponentFixture<FeedUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let feedFormService: FeedFormService;
  let feedService: FeedService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FeedUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(FeedUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FeedUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    feedFormService = TestBed.inject(FeedFormService);
    feedService = TestBed.inject(FeedService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const feed: IFeed = { id: 456 };

      activatedRoute.data = of({ feed });
      comp.ngOnInit();

      expect(comp.feed).toEqual(feed);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFeed>>();
      const feed = { id: 123 };
      jest.spyOn(feedFormService, 'getFeed').mockReturnValue(feed);
      jest.spyOn(feedService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ feed });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: feed }));
      saveSubject.complete();

      // THEN
      expect(feedFormService.getFeed).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(feedService.update).toHaveBeenCalledWith(expect.objectContaining(feed));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFeed>>();
      const feed = { id: 123 };
      jest.spyOn(feedFormService, 'getFeed').mockReturnValue({ id: null });
      jest.spyOn(feedService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ feed: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: feed }));
      saveSubject.complete();

      // THEN
      expect(feedFormService.getFeed).toHaveBeenCalled();
      expect(feedService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IFeed>>();
      const feed = { id: 123 };
      jest.spyOn(feedService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ feed });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(feedService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
