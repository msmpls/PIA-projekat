import { Routes } from '@angular/router';

export const routes: Routes = [
  // 1. Root putanja vodi na 'home'
  { path: '', redirectTo: 'home', pathMatch: 'full' },

  // 2. Home ruta
  { 
    path: 'home', 
    loadComponent: () => import('./features/public/home/home').then(m => m.Home) 
  },

  // 3. Login ruta (ostaje ista)
  { 
    path: 'login', 
    loadComponent: () => import('./features/auth/login/login').then(m => m.Login) 
  },

  // ... ostale rute (register, search, itd.)
  {path: 'register',
    loadComponent: ()=> import('./features/auth/register/register').then(m => m.Register)
  },
  // 4. Catch-all ruta (ako ukucaju nešto pogrešno, vraćamo na home)
  { path: '**', redirectTo: 'home' }
];