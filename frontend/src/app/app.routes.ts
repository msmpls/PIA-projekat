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
    path: 'facility/:id',
    loadComponent: () => import('./features/public/facility-details/facility-details').then(m => m.FacilityDetails)
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

  // ===== Athlete routes =====
  {
    path: 'profile',
    loadComponent: () => import('./features/user/profile/profile').then(m => m.Profile),
    canActivate: [authGuard]
  },

  {
    path: 'my-reservations',
    loadComponent: () => import('./features/user/profile/profile').then(m => m.Profile),
    canActivate: [authGuard]
  },

  {
    path: 'shop',
    loadComponent: () => import('./features/user/shop/shop').then(m => m.Shop),
    canActivate: [authGuard]
  },

  {
    path: 'teammate-ads',
    loadComponent: () => import('./features/user/teammate-ads/teammate-ads').then(m => m.TeammateAds),
    canActivate: [authGuard]
  },

  {
    path: 'trainings',
    loadComponent: () => import('./features/user/trainings/trainings').then(m => m.Trainings),
    canActivate: [authGuard]
  },

  {
    path: 'statistics',
    loadComponent: () => import('./features/user/statistics/statistics').then(m => m.Statistics),
    canActivate: [authGuard]
  },

  // ===== Employee routes =====
  {
    path: 'employee/dashboard',
    loadComponent: () => import('./features/employee/dashboard/dashboard').then(m => m.Dashboard),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['EMPLOYEE'] }
  },

  {
    path: 'employee/facilities',
    loadComponent: () => import('./features/employee/facilities/facilities').then(m => m.Facilities),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['EMPLOYEE'] }
  },

  {
    path: 'employee/equipment-promotions',
    loadComponent: () => import('./features/employee/equipment-promotions/equipment-promotions').then(m => m.EquipmentPromotions),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['EMPLOYEE'] }
  },

  {
    path: 'employee/calendar',
    loadComponent: () => import('./features/employee/calendar/calendar').then(m => m.Calendar),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['EMPLOYEE'] }
  },

  {
    path: 'employee/orders',
    loadComponent: () => import('./features/employee/orders/orders').then(m => m.EmployeeOrders),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['EMPLOYEE'] }
  },

  {
    path: 'employee/reports',
    loadComponent: () => import('./features/employee/reports/reports').then(m => m.Reports),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['EMPLOYEE'] }
  },

  // ===== Admin routes =====
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
