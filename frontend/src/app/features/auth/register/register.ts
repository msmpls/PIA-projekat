import { Component, inject, signal, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { AuthService } from '../../../core/auth/auth.service';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private http = inject(HttpClient);

  // Fajl koji korisnik bira
  private selectedFile: File | null = null;
  errorMessage = signal<string>('');
  sports = signal<string[]>([]);
  sportsLoading = signal<boolean>(false);

  // DiceBear avatar
  avatarPreview = signal<string>('');
  avatarSeed = signal<string>('');
  avatarStyle = signal<string>('identicon');

  avatarStyles = [
    { value: 'identicon', label: 'Geometrija' },
    { value: 'bottts', label: 'Roboti' },
    { value: 'initials', label: 'Inicijali' },
  ];

  registerForm = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.pattern(/^(?=.*[A-Z])(?=.*\d)(?=.*[^a-zA-Z0-9])[A-Za-z].{7,11}$/)]],
    first_name: ['', Validators.required],
    last_name: ['', Validators.required],
    phone: ['', [Validators.required, Validators.pattern(/^\d{9,15}$/)]],
    role: ['ATHLETE', Validators.required],
    favorite_sports: [[] as string[], [this.favoriteSportsValidator]],
    facility_name: [''],
    company_address: [''],
    company_id_number: [''],
    pib: ['']
  });

  get isEmployee() {
    return this.registerForm.get('role')?.value === 'EMPLOYEE';
  }

  ngOnInit() {
    this.loadSports();
  }

  loadSports() {
    this.sportsLoading.set(true);
    this.http.get<string[]>('http://localhost:8080/api/sports/names').subscribe({
      next: names => {
        this.sports.set(names);
        this.sportsLoading.set(false);
      },
      error: () => {
        this.sports.set([]);
        this.sportsLoading.set(false);
      }
    });
  }

  favoriteSportsValidator(control: AbstractControl) {
    const value = control.value;
    return Array.isArray(value) && value.length > 0 ? null : { required: true };
  }

  onSportToggle(event: Event) {
    const input = event.target as HTMLInputElement;
    const control = this.registerForm.get('favorite_sports') as unknown as FormControl<string[]>;
    const currentValue = control.value ?? [];
    const updated = input.checked
      ? [...currentValue, input.value]
      : currentValue.filter(sport => sport !== input.value);

    control.setValue(updated);
    control.markAsTouched();
  }

  isSportSelected(sport: string): boolean {
    const control = this.registerForm.get('favorite_sports') as AbstractControl;
    return Array.isArray(control.value) && control.value.includes(sport);
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      this.avatarPreview.set('');
    }
  }

  // ===== DiceBear avatar generation =====
  generateAvatar() {
    // Random seed based on username or random string
    const username = this.registerForm.get('username')?.value || '';
    const seed = username ? username + '_' + Date.now() : 'user_' + Math.random().toString(36).substring(7);
    this.avatarSeed.set(seed);
    this.updateAvatarPreview();
  }

  onAvatarStyleChange(event: Event) {
    const select = event.target as HTMLSelectElement;
    this.avatarStyle.set(select.value);
    if (this.avatarSeed()) {
      this.updateAvatarPreview();
    }
  }

  private updateAvatarPreview() {
    const seed = this.avatarSeed();
    const style = this.avatarStyle();
    // DiceBear HTTP API - returns PNG image
    this.avatarPreview.set(`https://api.dicebear.com/7.x/${style}/png?seed=${encodeURIComponent(seed)}&size=200`);
    // Clear any manually selected file
    this.selectedFile = null;
  }

  regenerateAvatar() {
    this.avatarSeed.set(this.avatarSeed() + '_v' + Math.floor(Math.random() * 1000));
    this.updateAvatarPreview();
  }

  saveAvatarAsProfilePic() {
    if (!this.avatarPreview()) return;
    // Fetch the PNG image and convert to File
    fetch(this.avatarPreview())
      .then(response => response.blob())
      .then(blob => {
        const file = new File([blob], `avatar_${this.avatarSeed()}.png`, { type: 'image/png' });
        this.selectedFile = file;
        this.errorMessage.set('');
      })
      .catch(() => {
        this.errorMessage.set('Greška pri preuzimanju avatara. Pokušajte ponovo.');
      });
  }

  onSubmit() {
    this.errorMessage.set('');

    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const formData = new FormData();
    const formValues = this.registerForm.value;

    if (this.isEmployee) {
      const requiredEmployeeFields = ['facility_name', 'company_address', 'company_id_number', 'pib'];
      for (const field of requiredEmployeeFields) {
        if (!formValues[field as keyof typeof formValues]) {
          this.errorMessage.set('Sva polja za zaposlenog su obavezna.');
          return;
        }
      }
    }

    // Dodajemo polja u FormData
    const employeeFields = ['facility_name', 'company_address', 'company_id_number', 'pib'];
    Object.keys(formValues).forEach(key => {
      if (!this.isEmployee && employeeFields.includes(key)) {
        return;
      }

      const value = formValues[key as keyof typeof formValues];
      if (value === null || value === undefined || value === '') {
        return;
      }
      if (Array.isArray(value)) {
        formData.append(key, value.join(','));
      } else {
        formData.append(key, String(value));
      }
    });

    // Dodajemo sliku ako postoji
    if (this.selectedFile) {
      formData.append('profile_image', this.selectedFile);
    }

    this.authService.register(formData).subscribe({
      next: () => this.router.navigate(['/pending-approval']),
      error: (err) => this.errorMessage.set(err?.error || 'Došlo je do greške pri registraciji.')
    });
  }
}
