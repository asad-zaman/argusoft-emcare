import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthGuard } from './auth/auth.guard';
import { HomeComponent } from './root/components/home/home.component';
import {
  LoginComponent,
  SignupComponent,
  // ManagePatientComponent,
  // ShowPatientComponent,
  // ManageOrganizationComponent,
  // ShowOrganizationComponent,
  LocationTypeComponent,
  LocationManagementComponent,
  ShowLocationComponent,
  ShowLocationTypeComponent
} from './root/index';

const routes: Routes = [
  { path: 'dashboard', component: HomeComponent, canActivate: [AuthGuard] },
  { path: 'login', component: LoginComponent, canActivate: [AuthGuard] },
  { path: 'signup', component: SignupComponent, canActivate: [AuthGuard] },
  // { path: 'addPatient', component: ManagePatientComponent, canActivate: [AuthGuard] },
  // { path: 'editPatient/:id', component: ManagePatientComponent, canActivate: [AuthGuard] },
  // { path: 'showPatient', component: ShowPatientComponent, canActivate: [AuthGuard] },
  // { path: 'addOrganization', component: ManageOrganizationComponent, canActivate: [AuthGuard] },
  // { path: 'editOrganization/:id', component: ManageOrganizationComponent, canActivate: [AuthGuard] },
  // { path: 'showOrganization', component: ShowOrganizationComponent, canActivate: [AuthGuard] },
  { path: 'addLocationType', component: LocationTypeComponent, canActivate: [AuthGuard] },
  { path: 'editLocationType/:id', component: LocationTypeComponent, canActivate: [AuthGuard] },
  { path: 'showLocationType', component: ShowLocationTypeComponent, canActivate: [AuthGuard] },
  { path: 'addLocation', component: LocationManagementComponent, canActivate: [AuthGuard] },
  { path: 'editLocation/:id', component: LocationManagementComponent, canActivate: [AuthGuard] },
  { path: 'showLocation', component: ShowLocationComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: 'login' }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
