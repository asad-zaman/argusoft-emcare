import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SharedModule } from './shared/shared.module';
import {
  LoginComponent,
  SignupComponent,
  LocationManagementComponent,
  LocationTypeComponent,
  LocationService,
  DeviceManagementComponent,
  DeviceManagementService,
  UserListComponent,
  ManageUserComponent,
  ConfirmUserComponent,
  FeatureListComponent,
  ManageFeatureComponent,
  ComparePatientComponent,
  UserManagementService,
  FeatureManagementService,
  ManageFacilityComponent,
  ShowLocationTypeComponent,
  ShowLocationComponent,
  DeviceListComponent,
  PatientListComponent,
  ManageRoleComponent,
  ShowRoleComponent,
  LocationFilterComponent,
  ManageProfileComponent,
  ManageTranslationsComponent,
  QuestionnaireListComponent,
  QuestionnaireBuilderComponent,
  LocationDropdownComponent,
  LanguageListComponent
} from './root/index';
import { AuthenticationService, ToasterService } from './shared';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { HomeComponent } from './root/components/home/home.component';
import { HTTPStatus, LaunguageSubjects, TokenInterceptor } from './auth/token-interceptor';
import { CommonModule } from '@angular/common';
import { NgxPaginationModule } from 'ngx-pagination';
import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BaseModule } from './base.module';
import { DropdownModule } from 'primeng/dropdown';
import { ShowFacilityComponent } from './root/components/Facility/show-facility/show-facility.component';
import { CheckboxModule } from 'primeng/checkbox';
import { MultiSelectModule } from 'primeng/multiselect';
import { TooltipModule } from 'primeng/tooltip';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    SignupComponent,
    LocationTypeComponent,
    LocationManagementComponent,
    ShowLocationTypeComponent,
    ShowLocationComponent,
    DeviceManagementComponent,
    DeviceListComponent,
    UserListComponent,
    ManageUserComponent,
    PatientListComponent,
    ManageRoleComponent,
    ShowRoleComponent,
    ConfirmUserComponent,
    LocationFilterComponent,
    FeatureListComponent,
    ManageFeatureComponent,
    ManageFeatureComponent,
    QuestionnaireListComponent,
    QuestionnaireBuilderComponent,
    ManageProfileComponent,
    ManageTranslationsComponent,
    ComparePatientComponent,
    ManageFacilityComponent,
    ShowFacilityComponent,
    LocationDropdownComponent,
    LanguageListComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    CommonModule,
    SharedModule,
    NgxPaginationModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot(),
    BaseModule.forRoot(),
    DropdownModule,
    CheckboxModule,
    MultiSelectModule,
    TooltipModule
  ],
  providers: [
    AuthenticationService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    },
    LocationService,
    DeviceManagementService,
    UserManagementService,
    FeatureManagementService,
    HTTPStatus,
    ToasterService,
    LaunguageSubjects
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
