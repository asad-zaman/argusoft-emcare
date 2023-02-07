import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationService, ToasterService } from 'src/app/shared';
import { MustMatch } from 'src/app/shared/validators/must-match.validator';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss']
})
export class ForgotPasswordComponent implements OnInit {

  forgotPasswordForm!: FormGroup;
  loading = false;
  submitted = false;
  returnUrl: string | undefined;
  error = '';
  isEmailInputsVisible = true;
  isOTPInputsVisible = false;
  isNewPasswordInputsVisible = false;
  showPassword = false;

  @ViewChild('passwordComponent') passwordComponent: ElementRef;
  @ViewChild('confirmpasswordComponent') confirmpasswordComponent: ElementRef;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly authService: AuthenticationService,
    private readonly router: Router,
    private readonly toasterService: ToasterService
  ) { }

  ngOnInit(): void {
    this.prerequisite();
  }

  prerequisite() {
    this.forgotPasswordForm = this.formBuilder.group({
      username: ['', [Validators.pattern('^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-]+$')]],
      otp: [''],
      password: [''],
      confirmPassword: ['']
    }, {
      validator: MustMatch('password', 'confirmPassword')
    });
  }

  get f() {
    return this.forgotPasswordForm.controls;
  }

  navigateToLogin() {
    this.router.navigate(["/login"]);
  }

  checkInputAndProceed() {
    this.submitted = true;
    // stop here if form is invalid
    if (this.forgotPasswordForm.invalid) {
      if (this.isNewPasswordInputsVisible) {
        this.toasterService.showToast('info', 'Password does not match', 'EMCARE!');
      }
      return;
    }
    if (this.isEmailInputsVisible && this.forgotPasswordForm.get('username').value) {
      this.generateOtpToVerifyUser();
    } else {
      this.toasterService.showToast('info', 'Please enter username', 'EMCARE!');
    }
    if (this.isOTPInputsVisible) {
      this.verifyOTP();
    }
    if (this.isNewPasswordInputsVisible) {
      this.resetPassword();
    }
  }

  generateOtpToVerifyUser() {
    this.authService.generateOTPFromUsername(this.forgotPasswordForm.value.username).subscribe(data => {
      if (data) {
        this.isEmailInputsVisible = false;
        this.isOTPInputsVisible = true;
        this.isNewPasswordInputsVisible = false;
      }
    });
  }

  verifyOTP() {
    const body = {
      emailId: this.forgotPasswordForm.value.username,
      otp: this.forgotPasswordForm.value.otp
    }
    this.authService.verifyOTP(body)
      .subscribe(data => {
        if (data) {
          this.isEmailInputsVisible = false;
          this.isOTPInputsVisible = false;
          this.isNewPasswordInputsVisible = true;
        }
      }, error => {
        this.toasterService.showToast('error', error.error.errorMessage, 'EMCARE!');
        if (error.error.errorMessage === 'OTP is expired. Please re-generate new OTP') {
          this.isEmailInputsVisible = true;
          this.isOTPInputsVisible = false;
          this.isNewPasswordInputsVisible = false;
          this.forgotPasswordForm.reset();
        }
      });
  }

  resetPassword() {
    const body = {
      emailId: this.forgotPasswordForm.value.username,
      otp: this.forgotPasswordForm.value.otp,
      password: this.forgotPasswordForm.value.password
    }
    this.authService.resetPassword(body).subscribe(data => {
      if (data) {
        this.navigateToLogin();
      }
    });
  }

  showPasswordToUser() {
    this.showPassword = !this.showPassword;
    if (this.showPassword) {
      this.passwordComponent.nativeElement.type = 'text';
      this.confirmpasswordComponent.nativeElement.type = 'text';
    } else {
      this.passwordComponent.nativeElement.type = 'password';
      this.confirmpasswordComponent.nativeElement.type = 'password';
    }
  }
}
