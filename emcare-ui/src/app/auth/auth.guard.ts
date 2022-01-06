import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthenticationService } from '../shared/services/authentication.service';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {

    user: any;
    result: boolean;

    constructor(
        private router: Router,
        private authService: AuthenticationService
    ) { }

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
                    this.result = this.checkFeature(route);
                    if (!this.result) {
                        this.router.navigate(['/dashboard']);
                    }
                    return true;
                })
                return true;
            }
        } else {
            if (route.routeConfig.path !== 'login' && route.routeConfig.path !== 'signup') {
                this.router.navigate(['/login']);
            } 
            return true;
        }
    }

    checkFeature(route) {
        if (
           route.routeConfig.path == 'addLocationType' 
        || route.routeConfig.path.includes('editLocationType')
        || route.routeConfig.path == 'showLocationType'
        || route.routeConfig.path == 'addLocation'
        || route.routeConfig.path.includes('editLocation')
        || route.routeConfig.path == 'showLocation') {
            return !!this.user.feature.find(f => f.menu_name === 'Location Management');
        } else if (
            route.routeConfig.path == 'showDevices'
        ) {
            return !!this.user.feature.find(f => f.menu_name === 'Device Management');
        } else if (
            route.routeConfig.path == 'showUsers'
        ||  route.routeConfig.path == 'addUser'
        ||  route.routeConfig.path.includes('updateUser')
        ||  route.routeConfig.path == 'confirmUsers'
        ) {
            return !!this.user.feature.find(f => f.menu_name === 'Users');
        } else if (
            route.routeConfig.path == 'showPatients'
        ) {
            return !!this.user.feature.find(f => f.menu_name === 'Patient Management');
        } else if (
            route.routeConfig.path == 'showRoles'
        ||  route.routeConfig.path == 'addRole'
        ||  route.routeConfig.path.includes('editRole')
        ) {
            return !!this.user.feature.find(f => f.menu_name === 'Roles');
        } else if (
            route.routeConfig.path == 'showFeatures'
        ||  route.routeConfig.path.includes('editFeature')
        ) {
            return !!this.user.feature.find(f => f.menu_name === 'Feature Management');
        } else {
            return false;
        }
    }
}