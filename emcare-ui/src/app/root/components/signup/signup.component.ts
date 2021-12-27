import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AuthenticationService } from 'src/app/shared';
import { Router } from '@angular/router';
import { first } from 'rxjs/operators';

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

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthenticationService,
    private router: Router
  ) { }

  ngOnInit() {
    this.signupForm = this.formBuilder.group({
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      username: ['', [Validators.required, Validators.pattern('^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-]+$')]],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required]
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
    this.authService.signup(this.signupForm.value.firstname, this.signupForm.value.lastname, this.signupForm.value.username, this.signupForm.value.password)
      .pipe(first())
      .subscribe(
        _data => {
          this.router.navigate(["/dashboard"]);
        },
        error => {
          this.error = error['error']['message'];
          this.loading = false;
        });
  }

  navigateToLogin() {
    this.router.navigate(["/login"]);
  }
}
