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
  LanguageListComponent,
  DuplicatePatientsComponent,
  ConsultationListComponent,
  ViewConsultationComponent,
  ManageCodeComponent,
  CodeListComponent,
  IndicatorComponent
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
import { ForgotPasswordComponent } from './root/components/forgot-password/forgot-password.component';
import { AdminPanelComponent } from './root/components/admin-panel/admin-panel.component';
import { AccordionModule } from 'primeng/accordion';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ChartModule } from 'angular-highcharts';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ManageOrganizationComponent } from './root/components/manage-organization/manage-organization.component';
import { OrganizationListComponent } from './root/components/organization-list/organization-list.component';
import { NgxIntlTelInputModule } from 'ngx-intl-tel-input';
import { IndicatorListComponent } from './root/components/indicator-list/indicator-list.component';
import { TenantListComponent } from './root/components/tenant-list/tenant-list.component';
import { ManageTenantComponent } from './root/components/manage-tenant/manage-tenant.component';
import { MessagesModule } from 'primeng/messages';
import { MessageModule } from 'primeng/message';
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
    LanguageListComponent,
    ForgotPasswordComponent,
    AdminPanelComponent,
    ManageOrganizationComponent,
    OrganizationListComponent,
    DuplicatePatientsComponent,
    ConsultationListComponent,
    ViewConsultationComponent,
    ManageCodeComponent,
    CodeListComponent,
    IndicatorComponent,
    IndicatorListComponent,
    TenantListComponent,
    ManageTenantComponent
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
    TooltipModule,
    AccordionModule,
    ButtonModule,
    CardModule,
    ChartModule,
    ToastModule,
    NgxIntlTelInputModule,
    MessagesModule,
    MessageModule
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
    LaunguageSubjects,
    MessageService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
