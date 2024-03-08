import { Component, Input, OnInit } from '@angular/core';

export interface CardStackData {
  image_url: string;
  title: string;
  description?: string;
}

@Component({
  selector: 'jhi-card-stack',
  templateUrl: './card-stack.component.html',
  styleUrls: ['./card-stack.component.scss'],
})
export class CardStackComponent implements OnInit {
  @Input() data: CardStackData[] | undefined;

  private selectedIndex: number = 0;
  private scrollAmount: number = 0;

  ngOnInit(): void {}

  getSelectedIndex() {
    return this.selectedIndex;
  }

  getSelectedCard() {
    if (!this.data) return undefined;
    return this.data[this.selectedIndex];
  }

  getStylesForIndex(i: number) {
    const offset = i - this.selectedIndex;

    return {
      zIndex: 10 - Math.abs(offset),
      left: 25 * Math.atan(offset) + '%',
      transform: 'scale(' + (1 - Math.abs(offset) / 10) + ')',
      boxShadow: offset === 0 ? 'black 0 0 2em 0.1em' : undefined,
    };
  }

  setSelectedIndex(i: number) {
    const maxIndex = this.data ? this.data.length - 1 : 0;
    this.selectedIndex = Math.max(0, Math.min(i, maxIndex));
  }

  canMoveLeft(): boolean {
    if (!this.data) return false;
    return this.selectedIndex > 0;
  }

  moveLeft(): void {
    this.setSelectedIndex(this.selectedIndex - 1);
  }

  canMoveRight(): boolean {
    if (!this.data) return false;
    return this.selectedIndex < this.data.length - 1;
  }

  moveRight(): void {
    this.setSelectedIndex(this.selectedIndex + 1);
  }

  onTouchStart(event: TouchEvent): void {
    this.scrollAmount = 0;
  }

  onWheelEvent(event: WheelEvent): void {
    event.preventDefault();
    const THRESHOLD = 20;

    if (Math.abs(event.deltaX) > Math.abs(event.deltaY)) {
      this.scrollAmount += event.deltaX;

      if (Math.abs(this.scrollAmount) > THRESHOLD) {
        if (this.scrollAmount > THRESHOLD) this.moveRight();
        if (this.scrollAmount < -THRESHOLD) this.moveLeft();
        this.scrollAmount = 0;
      }
    }
  }

  protected readonly Math = Math;
}
