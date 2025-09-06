import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-debate-card',
  templateUrl: './debate-card.component.html',
  styleUrls: ['./debate-card.component.scss']
})
export class DebateCardComponent {
  @Input() debate?: any;
  @Input() canPin = false;
  @Input() pinned = false;
  @Output() pin = new EventEmitter<void>();
  @Output() unpin = new EventEmitter<void>();
}
