import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

import { ReportPostRequest, ReportUserRequest, Report } from '../../shared/models/reports.model';
@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/reports`;
  reportPost(request: ReportPostRequest): Observable<Report> {
    return this.http.post<Report>(`${this.apiUrl}/post`, request);
  }
  reportUser(request: ReportUserRequest): Observable<Report> {
    return this.http.post<Report>(`${this.apiUrl}/user`, request);
  }

  submitReport(type: 'POST' | 'USER', targetId: number, reason: string): Observable<Report> {
    if (type === 'POST') {
      return this.reportPost({ reportedPostId: targetId, reason });
    } else {
      return this.reportUser({ reportedUserId: targetId, reason });
    }
  }


}