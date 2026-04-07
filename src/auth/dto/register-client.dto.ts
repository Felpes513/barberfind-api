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
import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';

export class RegisterClientDto {
  @ApiProperty({ example: 'Joao da Silva' })
  @IsString()
  @IsNotEmpty()
  @MaxLength(255)
  name!: string;

  @ApiProperty({ example: 'joao@barberfind.com' })
  @IsEmail()
  @MaxLength(255)
  email!: string;

  @ApiProperty({ example: 'SenhaForte123' })
  @IsString()
  @MinLength(6)
  @MaxLength(72)
  password!: string;

  @ApiPropertyOptional({ example: '+55 11 91234-5678' })
  @IsOptional()
  @Matches(/^[0-9+()\-\s]{10,20}$/, { message: 'invalid_phone' })
  phone?: string;

  @ApiPropertyOptional({ example: '1998-04-20' })
  @IsOptional()
  @IsDateString()
  birthDate?: string;

  @ApiPropertyOptional({ example: 'Cacheado' })
  @IsOptional()
  @IsString()
  @MaxLength(255)
  hairType?: string;

  @ApiPropertyOptional({ example: 'Fino' })
  @IsOptional()
  @IsString()
  @MaxLength(255)
  hairTexture?: string;

  @ApiPropertyOptional({ example: true })
  @IsOptional()
  @IsBoolean()
  hasBeard?: boolean;
}
