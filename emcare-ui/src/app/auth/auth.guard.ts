import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthenticationService } from '../shared/services/authentication.service';
@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {

    user: any;
    result: boolean;
    featureRouteArr: Object;

    constructor(
        private router: Router,
        private authService: AuthenticationService
    ) {
        this.featureRouteArr = {
            'Users': '/showUsers',
            'Location Management': '/showLocation',
            'Patient Management': '/showPatients',
            'Feature Management': '/showFeatures',
            'Roles': '/showRoles',
            'Device Management': '/showDevices',
            'Questionnaire Management': '/showQuestionnaires',
            'Language Management': '/manage-translation',
            'Manage Facility': '/showFacility'
        }
    }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
        const token = JSON.parse(localStorage.getItem('access_token'));
        const tokenExpiryDate = JSON.parse(localStorage.getItem('refresh_token_expiry_time'));
        const tokenExpiry = tokenExpiryDate
            ? new Date(tokenExpiryDate)
            : null;
        // Check if token is expired or not
        if (token) {
            if (tokenExpiry && tokenExpiry <= new Date()) {
                // token has expired user should benavigateByUrl logged out
                this.router.navigate(['/login']);
                localStorage.clear();
                return false;
            } else {
                this.authService.getLoggedInUser().subscribe(res => {
                    this.user = res;
                    this.getResultAndRedirect(route);
                    return true;
                });
                return true;
            }
        } else {
            if (route.routeConfig.path !== 'login' && route.routeConfig.path !== 'signup') {
                this.router.navigate(['/login']);
            }
            return true;
        }
    }

    getResultAndRedirect(route) {
        if (this.user.feature) {
            this.result = this.checkFeature(route);
            if (!this.result) {
                const route = this.getFeatureAndRedirectUser(this.user.feature[0].menu_name);
                this.router.navigate([route]);
            }
        }
    }

    getFeatureAndRedirectUser(feature) {
        return this.featureRouteArr[feature];
    }

    checkFeature(route) {
        if (
            route.routeConfig.path === 'addLocationType'
            || route.routeConfig.path.includes('editLocationType')
            || route.routeConfig.path === 'showLocationType'
            || route.routeConfig.path === 'addLocation'
            || route.routeConfig.path.includes('editLocation')
            || route.routeConfig.path === 'showLocation') {
            return !!this.user.feature.find(f => f.menu_name === 'Location Management');
        } else if (
            route.routeConfig.path == 'showDevices'
        ) {
            return !!this.user.feature.find(f => f.menu_name === 'Device Management');
        } else if (
            route.routeConfig.path === 'showUsers'
            || route.routeConfig.path === 'addUser'
            || route.routeConfig.path.includes('updateUser')
            || route.routeConfig.path === 'confirmUsers'
        ) {
            return !!this.user.feature.find(f => f.menu_name === 'Users');
        } else if (
            route.routeConfig.path === 'showPatients'
            || route.routeConfig.path === 'comparePatients'
        ) {
            return !!this.user.feature.find(f => f.menu_name === 'Patient Management');
        } else if (
            route.routeConfig.path == 'showQuestionnaires'
            || route.routeConfig.path === 'addQuestionnaire'
            || route.routeConfig.path.includes('updateQuestionnaire')
        ) {
            return !!this.user.feature.find(f => f.menu_name === 'Questionnaire Management');
        } else if (
            route.routeConfig.path === 'showRoles'
            || route.routeConfig.path === 'addRole'
            || route.routeConfig.path.includes('editRole')
        ) {
            return !!this.user.feature.find(f => f.menu_name === 'Roles');
        } else if (
            route.routeConfig.path === 'showFeatures'
            || route.routeConfig.path.includes('editFeature')
        ) {
            return !!this.user.feature.find(f => f.menu_name === 'Feature Management');
        } else if (
            route.routeConfig.path === 'manage-translation'
        ) {
            return !!this.user.feature.find(f => f.menu_name === 'Language Management');
        } else if (
            route.routeConfig.path === 'showFacility'
            || route.routeConfig.path.includes('manageFacility')
        ) {
            return !!this.user.feature.find(f => f.menu_name === 'Manage Facility');
        } else {
            return false;
        }
    }
}