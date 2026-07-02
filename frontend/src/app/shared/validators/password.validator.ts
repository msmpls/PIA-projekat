import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function passwordValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;

    //ako je polje prazno, ne validiramo (Validators.required)
    if (!value) return null;

    // Regex preuzet iz tvog Java koda
    const passwordRegex = /^(?=.*[A-Z])(?=.*\d)(?=.*[^a-zA-Z0-9])[a-zA-Z].{7,11}$/;

    const valid = passwordRegex.test(value);
    
    // Ako nije validno, vraćamo objekat greške
    return valid ? null : { invalidPassword: true };
  };
}