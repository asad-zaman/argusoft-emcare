import { Injectable } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class ToasterService {

  constructor(private readonly toaster: ToastrService) { }

  showSuccess(message, title?) {
    this.toaster.success(message, title);
  }

  showError(message, title?) {
    this.toaster.error(message, title);
  }

  showInfo(message, title?) {
    this.toaster.info(message, title);
  }

  showWarning(message, title?) {
    this.toaster.warning(message, title);
  }
}