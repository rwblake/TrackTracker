import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { GenreComponent } from './list/genre.component';
import { GenreDetailComponent } from './detail/genre-detail.component';
import { GenreUpdateComponent } from './update/genre-update.component';
import { GenreDeleteDialogComponent } from './delete/genre-delete-dialog.component';
import { GenreRoutingModule } from './route/genre-routing.module';

@NgModule({
  imports: [SharedModule, GenreRoutingModule],
  declarations: [GenreComponent, GenreDetailComponent, GenreUpdateComponent, GenreDeleteDialogComponent],
})
export class GenreModule {}
