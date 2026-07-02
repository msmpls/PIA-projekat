import { Component, inject, computed } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NgIf } from '@angular/common';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, NgIf],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar {
  private authService = inject(AuthService);

  // Signal iz servisa
  user = this.authService.currentUser;

  // Reaktivno izračunate vrednosti
  isLoggedIn = computed(() => this.authService.isLoggedIn());
  isGuest = computed(() => !this.isLoggedIn());
  isAdmin = computed(() => this.user()?.role === 'ADMIN');
  userName = computed(() => this.user()?.username ?? '');

  logout() {
    this.authService.logout();
  }
}