import { Injectable} from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { environment } from "../../../environments/environment";
import { Post,Page,CreatePostRequest } from "../../shared/models/post.model";
@Injectable({
    providedIn: 'root'
})
 
export class PostService{
    // private postCache = signal<Post[]>([]);
    constructor(private http: HttpClient) {}
//  geet posts of ppl whom you're following ;
    getFeed(page :number = 0 , size : number =10):Observable<Page<Post>> {
        const url = `${environment.apiUrl}${environment.apiEndpoints.posts.feed}`
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
        return this.http.get<Page<Post>>(url, { params });
    }
        // get les posts men taarf;

    getExploreFeed(page: number = 0, size: number = 10): Observable<Page<Post>> {
    const url = `${environment.apiUrl}/feed/explore`;
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<Page<Post>>(url, { params });
  }

//   get 1 Post by ID
    getPostById(postId : number) : Observable<Post> {
        const url =  `${environment.apiUrl}${environment.apiEndpoints.posts.base}/${postId}`;
        return this.http.get<Post>(url);
    }
            // get l postss for the profille 
    getPostsByAuthor(authorId: number, page: number = 0, size: number = 10): Observable<Page<Post>> {
    const url = `${environment.apiUrl}${environment.apiEndpoints.posts.base}/author/${authorId}`;
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<Page<Post>>(url, { params });
  }


//   create new post
    createPost(postData : CreatePostRequest) : Observable<Post> {
        const url = `${environment.apiUrl}${environment.apiEndpoints.posts.base}`;
        const formData = new FormData();
        formData.append("title", postData.title);
        formData.append("content", postData.content);
        if (postData.media){
            formData.append("media",postData.media)
        }
        return this.http.post<Post>(url, formData);
    }
        // now we should use the put method since w'll update the posts data;
     updatePost(postId: number, postData: CreatePostRequest): Observable<Post> {
    const url = `${environment.apiUrl}${environment.apiEndpoints.posts.base}/${postId}`;
    const formData = new FormData();
    
    formData.append('title', postData.title);
    formData.append('content', postData.content);
    if (postData.media) {
      formData.append('media', postData.media);
    }

    return this.http.put<Post>(url, formData);
  }

  deletePost(postId: number): Observable<void> {
    const url = `${environment.apiUrl}${environment.apiEndpoints.posts.base}/${postId}`;
    return this.http.delete<void>(url);
  }

  likePost(postId :number) :Observable<any>{
    const url = `${environment.apiUrl}${environment.apiEndpoints.posts.base}/${postId}/likes`;
    return this.http.post(url,{});
  }
  unlikePost(postId: number): Observable<any> {
    const url = `${environment.apiUrl}${environment.apiEndpoints.posts.base}/${postId}/likes`;
    return this.http.delete(url);
  }

//   get l comments 
getComments(postId: number, page: number = 0, size: number = 20): Observable<any> {
    const url = `${environment.apiUrl}${environment.apiEndpoints.posts.base}/${postId}/comments`;
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get(url, { params });
  }
       // http://localhost:8080/posts/1/comments/19
  deleteComment(postId: number,commentId: number): Observable<void> {
    const url = `${environment.apiUrl}${environment.apiEndpoints.posts.base}/${postId}/comments/${commentId}`;
    return this.http.delete<void>(url);
  }


 addComment(postId: number, content: string): Observable<any> {
    const url = `${environment.apiUrl}${environment.apiEndpoints.posts.base}/${postId}/comments`;
    return this.http.post(url, { content });
  }
   getMediaUrl(filename: string): string {
    return `${environment.apiUrl}/files/${filename}`;
  }
}