import { Injectable } from '@angular/core';
import { MessageService } from 'primeng/api';

@Injectable({
    providedIn: 'root'
})
export class ToasterService {

    constructor(
        private readonly messageService: MessageService
    ) { }

    showToast(severity, detail, summary) {
        this.messageService.add({ severity: severity, summary: summary, detail: detail });
    }
}
