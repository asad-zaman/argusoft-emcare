import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class DeviceManagementService {

    backendURL = `http://fbb0-14-192-29-30.ngrok.io/api/device`;

    constructor(private http: HttpClient) { }

    getHeaders() {
        const headerObj = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Headers': 'Content-Type',
                'Access-Control-Allow-Methods': 'GET,POST,OPTIONS,DELETE,PUT',
                'Authorization': 'Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJTRnZhNzFXc0N6YUhBakVrY2htQTBDWWlrSjZfMWpBSFo0Ym5iaFBlVjJzIn0.eyJleHAiOjE2Mzg0Njc1NDYsImlhdCI6MTYzODQ0OTU0NiwianRpIjoiOTllYjAwMWQtYjljOS00YTJmLTk0ZDQtYTg2NjcwYmM4ZGM4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL2F1dGgvcmVhbG1zL2VtY2FyZSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiJkZjI5NTQyYS0yYmVlLTRhYjQtYTJlZC1hMzJjNjhlOWNiZDAiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJsb2dpbi1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiOGUwNTYyOWEtMmUwMS00ZDhjLWE1M2MtODY5ZTNmOGViN2YzIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vNzgyYy0xNC0xOTItMjktMzAubmdyb2suaW8vKiIsIioiLCJodHRwOi8vKiIsImh0dHA6Ly9sb2NhbGhvc3Q6NDIwMCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiYWJjIiwidXNlcl9hZG1pbiIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iLCJkZWZhdWx0LXJvbGVzLWVtY2FyZSIsInVzZXIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJsb2dpbi1hcHAiOnsicm9sZXMiOlsidXNlcl9hZG1pbiIsInVzZXIiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsInNpZCI6IjhlMDU2MjlhLTJlMDEtNGQ4Yy1hNTNjLTg2OWUzZjhlYjdmMyIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicHJlZmVycmVkX3VzZXJuYW1lIjoidXNlcjEifQ.fCvLqrORudSNqMYVt6HGzp0JNEPptMI6nrEMKXZbK_85SrSzjkQbOAK2Sc8kgcoAZiGNlMADh43I4nVwZkaDaFdEozMPtgqJbJAnZsTo98rEVxDVtxmD0hSMh9MZC8P1zXTR_-G2vML6uqlluMeGycfaqyJCW1ije1YzAdKkG-T3BPfKTK0TP7-q_IXTki8b0iN2VBaCSKKAyNxi7s8FHvVKUXCCq4MWurIUkhEz9ajFqG-c_Asnk5LmcYqKQxy-n95vfKn49gkurBq4SOpNRlgvL7T2cRaijrlq1P97VuLcgUU_ILJBrtKd3FDNJ_UD7HWidyxQKXDkaqIsMO850w'
            })
        };
        return headerObj;
    }

    getAllDevices() {
        return this.http.get(`${this.backendURL}/all`, this.getHeaders());
    }

    updateDeviceById(obj) {
        return this.http.put(`${this.backendURL}/update`, obj, this.getHeaders());
    }
}
