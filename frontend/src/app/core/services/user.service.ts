import { Injectable , signal } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { User, UpdateProfileRequest } from "../../shared/models/user.model";



export interface UserPage {
  content: User[];
  totalElements: number;
  totalPages: number;
  last: boolean;
}

@Injectable({
    providedIn:"root"
})

export class UserService{
    constructor(private http: HttpClient){}


    // this is for listing the userrs 
    getAllUsers(page: number = 0, size: number = 20): Observable<UserPage> {
    const url = `${environment.apiUrl}/users/all`;
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<UserPage>(url, { params });
  }

  searchUsers(query: string, page: number = 0, size: number = 20): Observable<UserPage> {
    const url = `${environment.apiUrl}/users/search`;
    const params = new HttpParams()
      .set('query', query)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<UserPage>(url, { params });
  }

    //  get profile by userID
    getUserProfile(userId: number): Observable<User> {
      console.log("11111111111111111111111111111111",userId);
      
    const url = `${environment.apiUrl}${environment.apiEndpoints.users.profile}/${userId}`;
    return this.http.get<User>(url);
  }

    // get profile by Usrnmeee
   getUserByUsername(username: string): Observable<User> {
      console.log("222222222222222222222222",username);

    const url = `${environment.apiUrl}${environment.apiEndpoints.users.profile}/username/${username}`;
    return this.http.get<User>(url);
  }

//   an update requert for the pic;
updateAvatar(avatarFile: File): Observable<User>{
    const url = `${environment.apiUrl}${environment.apiEndpoints.users.me}/avatar`
    const formatData = new FormData;
    formatData.append("avatar",avatarFile)
    return this.http.put<User>(url,formatData);
}

//  same for the Profile
updateProfile(profileData: UpdateProfileRequest): Observable<User> {
    const url = `${environment.apiUrl}${environment.apiEndpoints.users.me}`;
    return this.http.put<User>(url, profileData);
  }
  

  followUser(userId: number): Observable<any> {
    const url = `${environment.apiUrl}/subscriptions/follow/${userId}`;
    return this.http.post(url, {});
  }
   unfollowUser(userId: number): Observable<any> {
    const url = `${environment.apiUrl}/subscriptions/unfollow/${userId}`;
    return this.http.delete(url);
  }

  getFollowers(userId: number): Observable<any> {
    const url = `${environment.apiUrl}/subscriptions/followers/${userId}`;
    return this.http.get(url);
  }

  getFollowing(userId: number): Observable<any> {
    const url = `${environment.apiUrl}/subscriptions/following/${userId}`;
    return this.http.get(url);
  }


   changePassword(passwordData: { currentPassword: string; newPassword: string }): Observable<any> {
    const url = `${environment.apiUrl}${environment.apiEndpoints.users.me}/password`;
    return this.http.put(url, passwordData);
  }

}