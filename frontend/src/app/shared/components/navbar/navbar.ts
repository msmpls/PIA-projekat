import { Component, inject, computed } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar {
  private authService = inject(AuthService);

  // Signal iz servisa
  user = this.authService.currentUser; 

  // Reaktivno izračunate vrednosti
  isLoggedIn = computed(() => !!this.user());
  isAdmin = computed(() => this.user()?.role === 'ADMIN');

  logout() {
    this.authService.logout();
  }
}