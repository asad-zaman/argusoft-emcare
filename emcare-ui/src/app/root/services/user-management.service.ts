import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserManagementService {

  userURL = `http://localhost:8080/api/user`;

  constructor(private http: HttpClient) { }

  getHeaders() {
    const headerObj = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Headers': 'Content-Type',
        'Access-Control-Allow-Methods': 'GET,POST,OPTIONS,DELETE,PUT',
        'Authorization': 'Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJFR2tMZVFWdURxQnJxcHZqOG1RYXBZVTdYU1JoR092TEtUOW1KOWNDZTlZIn0.eyJleHAiOjE2Mzk3NDUxODEsImlhdCI6MTYzOTc0NDg4MSwianRpIjoiOWE0N2Q0ODUtZjRiMi00OWVhLWFiNjMtMDRlMmMzZTYzNjYyIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL2F1dGgvcmVhbG1zL2VtY2FyZSIsImF1ZCI6WyJyZWFsbS1tYW5hZ2VtZW50IiwiYWNjb3VudCJdLCJzdWIiOiI3Y2UwYzhiMi1kNDYzLTRlOWUtYWMxZS03ZmEwYzg3ZDVhMTYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJsb2dpbi1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiNDFlZDNkNmQtMDQzMy00MDM0LTgyMTYtOTJhMjQ0MmFiYWVhIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgxODAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJkZWZhdWx0LXJvbGVzLWVtY2FyZSIsInVzZXIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJyZWFsbS1tYW5hZ2VtZW50Ijp7InJvbGVzIjpbInZpZXctaWRlbnRpdHktcHJvdmlkZXJzIiwidmlldy1yZWFsbSIsIm1hbmFnZS1pZGVudGl0eS1wcm92aWRlcnMiLCJpbXBlcnNvbmF0aW9uIiwicmVhbG0tYWRtaW4iLCJjcmVhdGUtY2xpZW50IiwibWFuYWdlLXVzZXJzIiwicXVlcnktcmVhbG1zIiwidmlldy1hdXRob3JpemF0aW9uIiwicXVlcnktY2xpZW50cyIsInF1ZXJ5LXVzZXJzIiwibWFuYWdlLWV2ZW50cyIsIm1hbmFnZS1yZWFsbSIsInZpZXctZXZlbnRzIiwidmlldy11c2VycyIsInZpZXctY2xpZW50cyIsIm1hbmFnZS1hdXRob3JpemF0aW9uIiwibWFuYWdlLWNsaWVudHMiLCJxdWVyeS1ncm91cHMiXX0sImxvZ2luLWFwcCI6eyJyb2xlcyI6WyJ1c2VyIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiI0MWVkM2Q2ZC0wNDMzLTQwMzQtODIxNi05MmEyNDQyYWJhZWEiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3QifQ.fRTaRrGRLjJIXXEBkXuJN9EKtk6p2du2kWSNdP5Kbhb91nC7zu72D5Png7WKzs6LUpnrRpjQr9SWT4liSPO4hZ8AESVnp1iBBd5LIoMWEphvPGzhsZpifscZHDtsD3JCkjwLp76wi9nNxHkbkneErDJ0E0X3YbpXM-yh-qxhizN36HoU-QueFznUuIlWe6fxTahSMBt4xTSiuEehuPsEd1YnSjcpm3I4d0VoxcMeYrAz4B6oPsQ6_OLGNsVqfuxPAsAQi9g7a0qPumlspj35h426j4ZmQjwAzAXrAaNYWqFeWTCBS0REXXWNF-lKgKME44xqyOae5ZBJx60_Sy6tMA'
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
