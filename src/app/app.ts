import { Component } from '@angular/core';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { RouterOutlet } from '@angular/router';
import { Toast } from 'primeng/toast';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Toast, ConfirmDialog],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {}
