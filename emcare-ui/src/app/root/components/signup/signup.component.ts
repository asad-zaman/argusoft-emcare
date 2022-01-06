import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AuthenticationService } from 'src/app/shared';
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
  error = '';
  locationArr: any = [];
  roleArr: any = [];

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthenticationService,
    private router: Router
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
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required],
      location: ['', Validators.required],
      role: ['', Validators.required]
    }, {
      validator: MustMatch('password', 'confirmPassword')
  });
  }

  getAllLocations() {
    this.authService.getAllLocationsForSignUp().subscribe(res => {
      if (res) {
        this.locationArr = res;
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
    if (this.signupForm.invalid) {
      return;
    }
    this.authService.signup(this.signupForm.value.firstname, this.signupForm.value.lastname, 
      this.signupForm.value.username, this.signupForm.value.password,
      this.signupForm.value.location, this.signupForm.value.role
      )
      .pipe(first())
      .subscribe(
        _data => {
          this.router.navigate(["/login"]);
        },
        error => {
          this.error = error['error']['errorMessage'];
          //TODO: Add toaster for email already registered
          this.loading = false;
        });
  }

  navigateToLogin() {
    this.router.navigate(["/login"]);
  }
}
