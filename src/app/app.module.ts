import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SharedModule } from './shared/shared.module';
import { LoginComponent, SignupComponent } from './root/index';
import { AuthenticationService } from './shared';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { HomeComponent } from './root/components/home/home.component';
import { tempBackendProvider } from './auth/temp-backend';
import { CommonModule } from '@angular/common';
@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    SignupComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    CommonModule,
    SharedModule
  ],
  providers: [
    AuthenticationService,
    tempBackendProvider
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
