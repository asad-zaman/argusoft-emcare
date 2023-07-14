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
  IndicatorComponent,
  IndicatorListComponent,
  ForgotPasswordComponent,
  AdminPanelComponent,
  ManageOrganizationComponent,
  OrganizationListComponent,
  HomeComponent,
  TermsConditionsComponent,
  LocationSubjects,
  FeatureSubjects,
  TenantListComponent,
  ShowFacilityComponent,
  ManageTenantComponent,
  AddLogComponent,
  LogListComponent
} from './root/index';
import { AuthenticationService, ToasterService } from './shared';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { HTTPStatus, LaunguageSubjects, TokenInterceptor } from './auth/token-interceptor';
import { CommonModule, DatePipe } from '@angular/common';
import { NgxPaginationModule } from 'ngx-pagination';
import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BaseModule } from './base.module';
import { DropdownModule } from 'primeng/dropdown';
import { CheckboxModule } from 'primeng/checkbox';
import { MultiSelectModule } from 'primeng/multiselect';
import { TooltipModule } from 'primeng/tooltip';
import { AccordionModule } from 'primeng/accordion';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ChartModule as primeCharts } from 'primeng/chart';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { NgxIntlTelInputModule } from 'ngx-intl-tel-input';
import { MessagesModule } from 'primeng/messages';
import { MessageModule } from 'primeng/message';
import { ColorPickerModule } from 'primeng/colorpicker';
import { CalendarModule } from 'primeng/calendar';
import { InputTextareaModule } from 'primeng/inputtextarea';

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
    ManageTenantComponent,
    TermsConditionsComponent,
    AddLogComponent,
    LogListComponent
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
    ToastModule,
    NgxIntlTelInputModule,
    MessagesModule,
    MessageModule,
    ColorPickerModule,
    CalendarModule,
    InputTextareaModule,
    primeCharts
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
    LocationSubjects,
    DatePipe,
    FeatureSubjects
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
