import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../../shared/models/user.model';
import { Post, Page } from '../../shared/models/post.model';
export interface DashboardStats {
  totalUsers: number;
  totalPosts: number;
  totalReports: number;
  pendingReports: number;
  totalLikes: number;
  totalComments: number;
  totalSubscriptions: number;
  reportsByStatus: {
    pending: number;
    reviewed: number;
    resolved: number;
    dismissed: number;
  };
}

export interface Report {
  id: number;
  reporterId: number;
  reporterUsername: string;
  reportedUserId?: number;
  reportedUsername?: string;
  reportedPostId?: number;
  reportedPostTitle?: string;
  reportType: 'USER' | 'POST';
  reason: string;
  status: 'PENDING' | 'REVIEWED' | 'RESOLVED' | 'DISMISSED';
  adminNotes?: string;
  reviewedBy?: number;
  reviewedByUsername?: string;
  createdAt: string;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
    private baseUrl = `${environment.apiUrl}/admin`;
    constructor(private  http: HttpClient) {}

    getDashboardStats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>(`${this.baseUrl}/stats`);
  }

  getAllUsers(page: number = 0, size: number = 20): Observable<Page<User>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<User>>(`${this.baseUrl}/users`, { params });
  }

  searchUsers(query: string, page: number = 0, size: number = 20): Observable<Page<User>> {
    const params = new HttpParams()
      .set('query', query)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<User>>(`${this.baseUrl}/users/search`, { params });
  }
  getUserById(userId: number): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/users/${userId}`);
  }
  deleteUser(userId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/users/${userId}`);
  }

   getAllPosts(page: number = 0, size: number = 20): Observable<Page<Post>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<Post>>(`${this.baseUrl}/posts`, { params });
  }

  deletePost(postId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/posts/${postId}`);
  }

   getAllReports(page: number = 0, size: number = 20): Observable<Page<Report>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<Report>>(`${this.baseUrl}/reports`, { params });
  }

  getPendingReports(page: number = 0, size: number = 20): Observable<Page<Report>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<Report>>(`${this.baseUrl}/reports/pending`, { params });
  }

  updateReport(reportId: number, status: string, adminNotes?: string): Observable<Report> {
    return this.http.put<Report>(`${this.baseUrl}/reports/${reportId}`, {
      status,
      adminNotes
    });
  }
  deleteReport(reportId: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/reports/${reportId}`);
  }

}