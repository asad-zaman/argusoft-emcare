import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserManagementService {

  userURL = `http://fbb0-14-192-29-30.ngrok.io/api/user`;

  constructor(private http: HttpClient) { }

  getHeaders() {
    const headerObj = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Headers': 'Content-Type',
        'Access-Control-Allow-Methods': 'GET,POST,OPTIONS,DELETE,PUT',
        'Authorization': 'Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJFR2tMZVFWdURxQnJxcHZqOG1RYXBZVTdYU1JoR092TEtUOW1KOWNDZTlZIn0.eyJleHAiOjE2Mzk5ODg5MDIsImlhdCI6MTYzOTk4ODYwMiwianRpIjoiNDlkMGE0ZGMtZTIyNS00MTU0LThkNTYtYzBmZWFiZDBkZGFmIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL2F1dGgvcmVhbG1zL2VtY2FyZSIsImF1ZCI6WyJyZWFsbS1tYW5hZ2VtZW50IiwiYWNjb3VudCJdLCJzdWIiOiI3Y2UwYzhiMi1kNDYzLTRlOWUtYWMxZS03ZmEwYzg3ZDVhMTYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJsb2dpbi1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiMGU5ZWJhNjEtMmYwNC00MWM2LThmNjktY2YyODFjMTcxMWI3IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgxODAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJkZWZhdWx0LXJvbGVzLWVtY2FyZSIsInVzZXIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJyZWFsbS1tYW5hZ2VtZW50Ijp7InJvbGVzIjpbInZpZXctaWRlbnRpdHktcHJvdmlkZXJzIiwidmlldy1yZWFsbSIsIm1hbmFnZS1pZGVudGl0eS1wcm92aWRlcnMiLCJpbXBlcnNvbmF0aW9uIiwicmVhbG0tYWRtaW4iLCJjcmVhdGUtY2xpZW50IiwibWFuYWdlLXVzZXJzIiwicXVlcnktcmVhbG1zIiwidmlldy1hdXRob3JpemF0aW9uIiwicXVlcnktY2xpZW50cyIsInF1ZXJ5LXVzZXJzIiwibWFuYWdlLWV2ZW50cyIsIm1hbmFnZS1yZWFsbSIsInZpZXctZXZlbnRzIiwidmlldy11c2VycyIsInZpZXctY2xpZW50cyIsIm1hbmFnZS1hdXRob3JpemF0aW9uIiwibWFuYWdlLWNsaWVudHMiLCJxdWVyeS1ncm91cHMiXX0sImxvZ2luLWFwcCI6eyJyb2xlcyI6WyJ1c2VyIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiIwZTllYmE2MS0yZjA0LTQxYzYtOGY2OS1jZjI4MWMxNzExYjciLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6InRlc3QifQ.OU7Gc6fPx2j2A_WTzW8PKt7mjR8WOj3kATu4dMSSFrdvOCYUlpLBUlaBIViDx7vcBQ-KgvwkT9Qmt8v_9TTa1M2PpfKzGCVzKxaPisEGMYwTx-OAOxXEeGzi0zTB-1vshlLCQ9I5nFbkq89xsKFhwzCzY90VpGCE-SlxcYmosLlm9UE4ltY7CUmRC7mc73bc9SxF1vNqbKWyZ8CikA3fb-vJSJY1p1vSXf34lxz5qmZW6CsnzwU_6PthVXvoP5PczUgxbTmYVSqau7hW76K8g4Nl2DSsili1M_AbWt_rpnPFcYcgqTSg1JqJmRJMyqk3yzUcHwojuYrtuhdA8-XoGQ'
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
