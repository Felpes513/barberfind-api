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
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class UpdateUserDto {
  @ApiPropertyOptional({ example: 'Pedro Lima' })
  @IsOptional()
  @IsString()
  @MaxLength(255)
  name?: string;

  @ApiPropertyOptional({ example: '+55 11 95555-4444' })
  @IsOptional()
  @Matches(/^[0-9+()\-\s]{10,20}$/, { message: 'invalid_phone' })
  phone?: string;

  @ApiPropertyOptional({ example: '1995-01-10' })
  @IsOptional()
  @IsDateString()
  birthDate?: string;

  @ApiPropertyOptional({ example: 'Liso' })
  @IsOptional()
  @IsString()
  @MaxLength(255)
  hairType?: string;

  @ApiPropertyOptional({ example: 'Grosso' })
  @IsOptional()
  @IsString()
  @MaxLength(255)
  hairTexture?: string;

  @ApiPropertyOptional({ example: true })
  @IsOptional()
  @IsBoolean()
  hasBeard?: boolean;

  @ApiPropertyOptional({ example: 'Barbeiro especializado em visagismo.' })
  @IsOptional()
  @IsString()
  bio?: string;

  @ApiPropertyOptional({ example: 10 })
  @IsOptional()
  @IsInt()
  @Min(0)
  yearsExperience?: number;
}

export class ChangePasswordDto {
  @ApiProperty({ example: 'SenhaAtual123' })
  @IsString()
  @IsNotEmpty()
  currentPassword!: string;

  @ApiProperty({ example: 'NovaSenhaForte123' })
  @IsString()
  @IsNotEmpty()
  @MinLength(8)
  @MaxLength(128)
  newPassword!: string;
}

export class PaymentMethodDto {
  @ApiProperty({ example: 'STRIPE' })
  @IsString()
  @IsNotEmpty()
  @MaxLength(50)
  provider!: string;

  @ApiProperty({ example: 'cus_ABC123' })
  @IsString()
  @IsNotEmpty()
  @MaxLength(255)
  providerCustomerId!: string;

  @ApiProperty({ example: 'CREDIT' })
  @IsString()
  @IsNotEmpty()
  @Matches(/^(CREDIT|DEBIT)$/)
  cardType!: string;

  @ApiPropertyOptional({ example: '1234' })
  @IsOptional()
  @IsString()
  @Matches(/^[0-9]{4}$/)
  lastFourDigits?: string;

  @ApiPropertyOptional({ example: 'VISA' })
  @IsOptional()
  @IsString()
  @MaxLength(30)
  brand?: string;
}

export class UserPreferencesDto {
  @ApiPropertyOptional({ example: 'DARK' })
  @IsOptional()
  @IsString()
  @Matches(/^(LIGHT|DARK)$/)
  theme?: string;

  @ApiPropertyOptional({ example: 'pt-BR' })
  @IsOptional()
  @IsString()
  @MaxLength(10)
  @Matches(/^[a-z]{2}(-[A-Z]{2})?$/)
  language?: string;
}

export class AcceptTermsDto {
  @ApiProperty({ example: '2026.04' })
  @IsString()
  @IsNotEmpty()
  termsVersion!: string;

  @ApiProperty({ example: true })
  @IsBoolean()
  accepted!: boolean;
}

export class DocumentCreateDto {
  @ApiProperty({ example: 'CPF' })
  @IsString()
  @IsNotEmpty()
  @MaxLength(50)
  documentType!: string;

  @ApiProperty({ example: '12345678901' })
  @IsString()
  @IsNotEmpty()
  @MaxLength(50)
  documentNumber!: string;
}

export class DocumentUpdateDto {
  @ApiPropertyOptional({ example: 'RG' })
  @IsOptional()
  @IsString()
  @MaxLength(50)
  documentType?: string;

  @ApiPropertyOptional({ example: '123456789' })
  @IsOptional()
  @IsString()
  @MaxLength(50)
  documentNumber?: string;
}
