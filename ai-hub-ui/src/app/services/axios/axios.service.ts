import { Injectable } from '@angular/core';
import axios from 'axios';

@Injectable({
  providedIn: 'root',
})
export class AxiosService {
  constructor() {
    axios.defaults.baseURL = 'http://localhost:8080';
    axios.defaults.headers.post['Content-Type'] = 'application/json';
  }

  hasAdminRole(): boolean {
    const token = this.getAuthToken();
    if (token === null) {
      return false;
    }

    try {
      const decodedToken: any = jwt_decode(token);
      return decodedToken.roles.includes('ADMIN');
    } catch (error) {
      console.error('Invalid token:', error);
      return false;
    }
  }

  getAuthToken(): string | null {
    // Check if token is expired and remove it
    const token = window.localStorage.getItem('auth_token');
    if (token === null) {
      return null;
    }

    try {
      const decodedToken: any = jwt_decode(token);
      const currentTime = Date.now() / 1000;

      if (decodedToken.exp < currentTime) {
        window.localStorage.removeItem('auth_token');
        return null;
      }
    } catch (error) {
      console.error('Invalid token:', error);
      window.localStorage.removeItem('auth_token');
      return null;
    }

    return token;
  }

  setAuthToken(token: string | null): void {
    if (token !== null) {
      window.localStorage.setItem('auth_token', token);
    } else {
      window.localStorage.removeItem('auth_token');
    }
  }

  request(method: string, url: string, data?: any): Promise<any> {
    let headers: any = {};

    if (this.getAuthToken() !== null) {
      headers = { Authorization: 'Bearer ' + this.getAuthToken() };
    }

    return axios({
      method: method,
      url: url,
      data: data,
      headers: headers,
    });
  }
}

function jwt_decode(token: string): any {
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const jsonPayload = decodeURIComponent(
    atob(base64)
      .split('')
      .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
      .join('')
  );

  return JSON.parse(jsonPayload);
}
