#!/bin/bash
# collect_models.sh - sakuplja sve frontend model fajlove u jedan tekstualni output

cd ~/PIA/projekatJun

OUTPUT="frontend_models_dump.txt"
> "$OUTPUT"  # isprazni ako već postoji

FILES=(
  "./frontend/src/app/core/models/user.model.ts"
  "./frontend/src/app/core/models/promotion.model.ts"
  "./frontend/src/app/core/models/teammate-ad.model.ts"
  "./frontend/src/app/core/models/shop.model.ts"
  "./frontend/src/app/core/models/auth.model.ts"
  "./frontend/src/app/core/models/facility.model.ts"
  "./frontend/src/app/core/models/training.model.ts"
  "./frontend/src/app/core/models/homepage.model.ts"
  "./frontend/src/app/core/models/court.model.ts"
  "./frontend/src/app/core/models/sport.model.ts"
  "./frontend/src/app/core/models/reservation.model.ts"
)

for f in "${FILES[@]}"; do
  if [ -f "$f" ]; then
    echo "===== FILE: $f =====" >> "$OUTPUT"
    cat "$f" >> "$OUTPUT"
    echo -e "\n" >> "$OUTPUT"
  else
    echo "===== MISSING: $f =====" >> "$OUTPUT"
    echo -e "\n" >> "$OUTPUT"
  fi
done

echo "Gotovo. Output je u $OUTPUT"
echo "Broj linija: $(wc -l < "$OUTPUT")"
