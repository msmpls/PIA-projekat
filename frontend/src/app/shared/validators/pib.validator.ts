import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function pibValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    
    // Provera: 9 cifara i ne počinje nulom
    const pibRegex = /^[1-9]\d{8}$/;

    if (!value) return null; // Ako je polje prazno, 'required' validator će to uhvatiti

    const valid = pibRegex.test(value);
    return valid ? null : { invalidPib: true };
  };
}