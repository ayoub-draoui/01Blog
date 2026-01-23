import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, interval } from 'rxjs';
import { tap, startWith, switchMap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
export interface Notification {
  id: number;
  userId: number;
  actorId: number;
  actorUsername: string;
  actorFirstname: string;
  actorLastname: string;
  actorAvatar: string | null;
  type: 'NEW_POST' | 'NEW_FOLLOWER' | 'LIKE' | 'COMMENT';
  relatedPostId: number | null;
  relatedPostTitle: string | null;
  relatedCommentId: number | null;
  message: string;
  isRead: boolean;
  createdAt: string;
}

export interface NotificationPage {
  content: Notification[];
  totalElements: number;
  totalPages: number;
  last: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  unreadCount = signal<number>(0);
  constructor(private http: HttpClient) {}
  
  getNotifications(page: number = 0, size: number = 20): Observable<NotificationPage> {
    const url = `${environment.apiUrl}/notifications`;
    const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.http.get<NotificationPage>(url, { params });
  }


  getUnreadNotifications(page:number =0  , size: number= 20): Observable<NotificationPage> {
     const url = `${environment.apiUrl}/notifications/unread`;
      const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
       return this.http.get<NotificationPage>(url, { params });

  }

  getUnreadCount(): Observable<{count: number}>{
        const url = `${environment.apiUrl}/nonotifications/unread/count`
        return this.http.get<{count:number}>(url).pipe(
            tap(response => this.unreadCount.set(response.count))
        )
}

 markAsRead(notificationId: number): Observable<Notification> {
    const url = `${environment.apiUrl}/notifications/${notificationId}/read`;
    return this.http.put<Notification>(url, {});
  }

 markAllAsRead(): Observable<any> {
    const url = `${environment.apiUrl}/notifications/read-all`;
    return this.http.put(url, {});
  }


    // detete notification 
   deleteNotification(notificationId: number): Observable<any> {
    const url = `${environment.apiUrl}/notifications/${notificationId}`;
    return this.http.delete(url);
  }

//   since the real-time  notif fiha gliib teez bzaaf we gonnna hard code it so its gonne
// searcch for new comments avery d9ii9a or 30 sec
  startPolling(): Observable<{count:number}>{
    return interval(30000).pipe(
        startWith(0),switchMap(()=> this.getUnreadCount())
    )
  }
}
