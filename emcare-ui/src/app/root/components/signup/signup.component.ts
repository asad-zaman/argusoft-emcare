import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AuthenticationService, ToasterService } from 'src/app/shared';
import { Router } from '@angular/router';
import { first } from 'rxjs/operators';
import { MustMatch } from 'src/app/shared/validators/must-match.validator';
@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent implements OnInit {

  signupForm!: FormGroup;
  loading = false;
  submitted = false;
  returnUrl: string | undefined;
  error = null;
  facilityArr: any = [];
  roleArr: any = [];

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly authService: AuthenticationService,
    private readonly router: Router,
    private readonly toasterService: ToasterService
  ) { }

  ngOnInit() {
    this.initSignUpForm();
    this.getAllLocations();
    this.getAllRoles();
  }

  initSignUpForm() {
    this.signupForm = this.formBuilder.group({
      firstname: ['', [Validators.required, Validators.pattern('^[a-zA-z]*')]],
      lastname: ['', [Validators.required, Validators.pattern('^[a-zA-z]*')]],
      username: ['', [Validators.required, Validators.pattern('^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-]+$')]],
      password: ['', [Validators.required]],
      confirmPassword: ['', [Validators.required]],
      location: ['', [Validators.required]],
      role: ['', [Validators.required]]
    }, {
      validator: MustMatch('password', 'confirmPassword')
    });
  }

  getAllLocations() {
    this.authService.getAllFacilitiesForSignUp().subscribe(res => {
      if (res) {
        res.map(el => {
          this.facilityArr.push({ name: `${el.facilityName} - ${el.organizationName}`, id: el.facilityId });
        });
      }
    });
  }

  getAllRoles() {
    this.authService.getAllRolesForSignUp().subscribe(res => {
      if (res) {
        this.roleArr = res;
      }
    });
  }

  get f() {
    return this.signupForm.controls;
  }

  userSignup() {
    this.submitted = true;
    // stop here if form is invalid
    if (this.signupForm.valid) {
      const facilityIdArr = this.signupForm.value.location.map(l => l.id);
      this.authService.signup(this.signupForm.value.firstname, this.signupForm.value.lastname,
        this.signupForm.value.username, this.signupForm.value.password,
        facilityIdArr, this.signupForm.value.role.name
      ).pipe(first()).subscribe(_data => {
        this.router.navigate(["/login"]);
        this.toasterService.showToast('success', 'User added successfully!', 'EMCARE');
      }, error => {
        if (error.status == 400) {
          this.error = error['error']['errorMessage'];
        }
        this.loading = false;
        this.toasterService.showToast('error', 'Email already registered!', 'EMCARE');
      });
    }
  }

  navigateToLogin() {
    this.router.navigate(["/login"]);
  }
}
