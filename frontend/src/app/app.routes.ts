import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth-guard';
import { roleGuard } from './core/auth/role-guard';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },

  { 
    path: 'home', 
    loadComponent: () => import('./features/public/home/home').then(m => m.Home) 
  },

  { 
    path: 'search', 
    loadComponent: () => import('./features/public/search/search').then(m => m.Search) 
  },

  { 
    path: 'login', 
    loadComponent: () => import('./features/auth/login/login').then(m => m.Login) 
  },

  { 
    path: 'admin-login', 
    loadComponent: () => import('./features/auth/admin-login/admin-login').then(m => m.AdminLogin) 
  },

  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register').then(m => m.Register)
  },

  {
    path: 'pending-approval',
    loadComponent: () => import('./features/auth/pending-approval/pending-approval').then(m => m.PendingApproval)
  },

  {
    path: 'profile',
    loadComponent: () => import('./features/user/profile/profile').then(m => m.Profile),
    canActivate: [authGuard]
  },

  {
    path: 'my-reservations',
    loadComponent: () => import('./features/user/my-reservations/my-reservations').then(m => m.MyReservations),
    canActivate: [authGuard]
  },

  {
    path: 'admin',
    redirectTo: 'admin/facility-requests',
    pathMatch: 'full'
  },

  {
    path: 'admin/facility-requests',
    loadComponent: () => import('./features/admin/facility-request/facility-request').then(m => m.FacilityRequest),
    canActivate: [roleGuard],
    data: { roles: ['ADMIN'] }
  },

  {
    path: 'admin/reg-requests',
    loadComponent: () => import('./features/admin/reg-request/reg-request').then(m => m.RegRequest),
    canActivate: [roleGuard],
    data: { roles: ['ADMIN'] }
  },

  {
    path: 'admin/users',
    loadComponent: () => import('./features/admin/users/users').then(m => m.Users),
    canActivate: [roleGuard],
    data: { roles: ['ADMIN'] }
  },

  { path: '**', redirectTo: 'home' }
];
