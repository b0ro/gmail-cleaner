import {Component} from '@angular/core';
import {NzFormControlComponent, NzFormDirective, NzFormItemComponent} from 'ng-zorro-antd/form';
import {NzColDirective, NzRowDirective} from 'ng-zorro-antd/grid';
import {NzInputDirective, NzInputGroupComponent} from 'ng-zorro-antd/input';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {NzButtonComponent} from 'ng-zorro-antd/button';
import {NzIconDirective} from 'ng-zorro-antd/icon';
import {NzCheckboxComponent} from 'ng-zorro-antd/checkbox';
import {NzPageHeaderComponent} from 'ng-zorro-antd/page-header';
import {NzTableComponent} from 'ng-zorro-antd/table';
import {Message} from '../../message';
import {NzDividerComponent} from 'ng-zorro-antd/divider';
import { NzTableModule } from 'ng-zorro-antd/table';
import {NzSwitchComponent} from 'ng-zorro-antd/switch';

@Component({
  selector: 'app-welcome',
  standalone: true,
  templateUrl: './welcome.component.html',
  imports: [
    NzFormDirective,
    NzFormItemComponent,
    NzFormControlComponent,
    NzColDirective,
    NzInputDirective,
    ReactiveFormsModule,
    NzInputGroupComponent,
    NzButtonComponent,
    NzIconDirective,
    NzCheckboxComponent,
    NzRowDirective,
    NzPageHeaderComponent,
    NzTableComponent,
    NzDividerComponent,
    NzTableModule,
    NzSwitchComponent,
    FormsModule
  ],
  styleUrls: ['./welcome.component.css']
})
export class WelcomeComponent {
  messages: Message[] = [
    {
      "sender": "artur.borodziej@gmail.com",
      "subject": "Some important mail",
      "preview": "Lorem imsum dolor",
      "size": 23454
    },
    {
      "sender": "krystian.borodziej@gmail.com",
      "subject": "Tata chcę być youtuberem",
      "preview": "wtf co tam itd",
      "size": 23454
    },
  ]

  constructor() { }
}

interface Person {
  key: string;
  name: string;
  age: number;
  address: string;
}
