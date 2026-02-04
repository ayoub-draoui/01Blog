import { HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { AuthService } from "../services/auth.service";
export const authInterceptor : HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService)
    const token = authService.getToken();
    if (req.url.includes("/auth/login") || req.url.includes("/auth/register")){
        return next(req);
    }


    if (token && ! authService.isTokenExpired()){
        console.log("im here this is comming from auth intercepters");
        console.log(req.url);
        
        const cloneReq = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });
        return next(cloneReq);
    }
    return next(req);
}