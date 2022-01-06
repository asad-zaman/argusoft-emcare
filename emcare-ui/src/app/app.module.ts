import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SharedModule } from './shared/shared.module';
import {
  LoginComponent,
  SignupComponent,
  // ManagePatientComponent,
  // ShowPatientComponent,
  // ManageOrganizationComponent,
  // ShowOrganizationComponent,
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
  UserManagementService,
  FeatureManagementService
} from './root/index';
import { AuthenticationService } from './shared';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { HomeComponent } from './root/components/home/home.component';
import { tempBackendProvider } from './auth/temp-backend';
import { CommonModule } from '@angular/common';
// import { FhirService } from './root/services/fhir.service';
import { ShowLocationTypeComponent } from './root/components/Location/location-type/show-location-type/show-location-type.component';
import { ShowLocationComponent } from './root/components/Location/location-management/show-location/show-location.component';
import { DeviceListComponent } from './root/components/Device-Management/device-list/device-list.component';
import { PatientListComponent } from './root/components/patient-management/patient-list/patient-list.component';
import { ManageRoleComponent } from './root/components/role-management/manage-role/manage-role.component';
import { ShowRoleComponent } from './root/components/role-management/show-role/show-role.component';
import { LocationFilterComponent } from './root/components/Location/location-filter/location-filter.component';
@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    SignupComponent,
    // ManagePatientComponent,
    // ShowPatientComponent,
    // ManageOrganizationComponent,
    // ShowOrganizationComponent,
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
    ManageFeatureComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    CommonModule,
    SharedModule
  ],
  providers: [
    AuthenticationService,
    tempBackendProvider,
    LocationService,
    DeviceManagementService,
    UserManagementService,
    FeatureManagementService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
