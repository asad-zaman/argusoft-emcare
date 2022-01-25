import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { AuthenticationService, ToasterService } from 'src/app/shared';
import { UserManagementService } from '../../services/user-management.service';

@Component({
  selector: 'app-manage-profile',
  templateUrl: './manage-profile.component.html',
  styleUrls: ['./manage-profile.component.scss']
})
export class ManageProfileComponent implements OnInit {

  userId;
  currentUserForm: FormGroup;
  submitted: boolean;
  locationId;

  constructor(
    private readonly authenticationService: AuthenticationService,
    private readonly userService: UserManagementService,
    private readonly formBuilder: FormBuilder,
    private readonly toasterService: ToasterService,
    private readonly translate: TranslateService,
    private readonly router: Router
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.initForm();
    this.getLoggedInUserId();
  }

  initForm() {
    this.currentUserForm = this.formBuilder.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      language: ['', [Validators.required]]
    });
  }

  getLoggedInUserId() {
    this.authenticationService.getLoggedInUser().subscribe(res => {
      if (res) {
        this.userId = res['userId'];
        this.currentUserForm.patchValue({
          language: res['language']
        });
        this.locationId = res['location']['id'];
        this.getLoggedInUserData();
      }
    });
  }

  getLoggedInUserData() {
    this.userService.getUserById(this.userId).subscribe(data => {
      if (data) {
        this.currentUserForm.patchValue({
          firstName: data['firstName'],
          lastName: data['lastName']
        });
      }
    })
  }

  get f() {
    return this.currentUserForm.controls;
  }

  saveData() {
    this.submitted = true;
    if (this.currentUserForm.valid) {
      const data = {
        firstName: this.f.firstName.value,
        lastName: this.f.lastName.value,
        language: this.f.language.value,
        locationId: this.locationId
      }
      this.userService.updateUser(data, this.userId).subscribe(() => {
        this.translate.use(this.f.language.value);
        localStorage.setItem('language', this.f.language.value);
        this.router.navigate(['/showUsers']);
        this.toasterService.showSuccess('User profile updated successfully!', 'EMCARE');
      });
    }
  }
}
