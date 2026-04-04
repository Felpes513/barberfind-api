import {
  IsBoolean,
  IsDateString,
  IsInt,
  IsNotEmpty,
  IsOptional,
  IsString,
  Matches,
  MaxLength,
  Min,
  MinLength,
} from 'class-validator';

export class UpdateUserDto {
  @IsOptional()
  @IsString()
  @MaxLength(255)
  name?: string;

  @IsOptional()
  @Matches(/^[0-9+()\-\s]{10,20}$/, { message: 'invalid_phone' })
  phone?: string;

  @IsOptional()
  @IsDateString()
  birthDate?: string;

  @IsOptional()
  @IsString()
  @MaxLength(255)
  hairType?: string;

  @IsOptional()
  @IsString()
  @MaxLength(255)
  hairTexture?: string;

  @IsOptional()
  @IsBoolean()
  hasBeard?: boolean;

  @IsOptional()
  @IsString()
  bio?: string;

  @IsOptional()
  @IsInt()
  @Min(0)
  yearsExperience?: number;
}

export class ChangePasswordDto {
  @IsString()
  @IsNotEmpty()
  currentPassword!: string;

  @IsString()
  @IsNotEmpty()
  @MinLength(8)
  @MaxLength(128)
  newPassword!: string;
}

export class PaymentMethodDto {
  @IsString()
  @IsNotEmpty()
  @MaxLength(50)
  provider!: string;

  @IsString()
  @IsNotEmpty()
  @MaxLength(255)
  providerCustomerId!: string;

  @IsString()
  @IsNotEmpty()
  @Matches(/^(CREDIT|DEBIT)$/)
  cardType!: string;

  @IsOptional()
  @IsString()
  @Matches(/^[0-9]{4}$/)
  lastFourDigits?: string;

  @IsOptional()
  @IsString()
  @MaxLength(30)
  brand?: string;
}

export class UserPreferencesDto {
  @IsOptional()
  @IsString()
  @Matches(/^(LIGHT|DARK)$/)
  theme?: string;

  @IsOptional()
  @IsString()
  @MaxLength(10)
  @Matches(/^[a-z]{2}(-[A-Z]{2})?$/)
  language?: string;
}

export class AcceptTermsDto {
  @IsString()
  @IsNotEmpty()
  termsVersion!: string;

  @IsBoolean()
  accepted!: boolean;
}

export class DocumentCreateDto {
  @IsString()
  @IsNotEmpty()
  @MaxLength(50)
  documentType!: string;

  @IsString()
  @IsNotEmpty()
  @MaxLength(50)
  documentNumber!: string;
}

export class DocumentUpdateDto {
  @IsOptional()
  @IsString()
  @MaxLength(50)
  documentType?: string;

  @IsOptional()
  @IsString()
  @MaxLength(50)
  documentNumber?: string;
}
