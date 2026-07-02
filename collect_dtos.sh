#!/bin/bash
# collect_dtos.sh - sakuplja sve DTO i model fajlove u jedan tekstualni output

cd ~/PIA/projekatJun

OUTPUT="dtos_models_dump.txt"
> "$OUTPUT"  # isprazni ako već postoji

FILES=(
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/dtos/ReservationResponse.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/dtos/ReservationRequest.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/dtos/TrainingResponse.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/dtos/RegisterRequest.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/dtos/AdminUserResponse.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/dtos/CartItemRequest.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/dtos/OrderResponse.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/dtos/TeammateAdDTOs.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/dtos/FacilityRequest.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/dtos/EmployeeProfileResponse.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/dtos/LoginRequest.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/dtos/AuthResponse.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/dtos/HomepageResponse.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/dtos/OrderRequest.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/dtos/TrainingRequest.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/dtos/ReservationViewResponse.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/models/Reservation.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/models/Sport.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/models/OrderItem.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/models/TeammateAdResponse.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/models/Court.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/models/Equipment.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/models/Facility.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/models/Order.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/models/Promotion.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/models/User.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/models/Coach.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/models/Training.java"
  "./backend_SpringBoot/backend/src/main/java/com/example/backend/models/TeammateAd.java"
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
