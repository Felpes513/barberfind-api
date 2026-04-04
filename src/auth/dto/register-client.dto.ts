import {
  IsBoolean,
  IsDateString,
  IsEmail,
  IsNotEmpty,
  IsOptional,
  IsString,
  Matches,
  MaxLength,
  MinLength,
} from 'class-validator';

export class RegisterClientDto {
  @IsString()
  @IsNotEmpty()
  @MaxLength(255)
  name!: string;

  @IsEmail()
  @MaxLength(255)
  email!: string;

  @IsString()
  @MinLength(6)
  @MaxLength(72)
  password!: string;

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
}
