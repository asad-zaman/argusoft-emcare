import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserManagementService {

  userURL = `${environment.apiUrl}/api/user`;

  constructor(private http: HttpClient) { }

  getHeaders() {
    let authToken = localStorage.getItem("access_token");
    authToken = authToken.substring(1,authToken.length - 1);
    const headerObj = {
        headers: new HttpHeaders({
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Headers': 'Content-Type',
            'Access-Control-Allow-Methods': 'GET,POST,OPTIONS,DELETE,PUT',
            'Authorization': `Bearer ${authToken}`
        })
    };
    return headerObj;
  }

  // User Endpoints

  createUser(user) {
    return this.http.post(`${this.userURL}/add`, user, this.getHeaders());
  }

  getAllUsers() {
    return this.http.get(`${this.userURL}/all`, this.getHeaders());
  }

  getUserById(id) {
    return this.http.get(`${this.userURL}/${id}`, this.getHeaders());
  }
  
  updateUser(user, id) {
    return this.http.put(`${this.userURL}/update/${id}`, user, this.getHeaders());
  }

}
