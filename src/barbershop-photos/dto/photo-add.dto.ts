import { IsNotEmpty, IsString } from 'class-validator';
import { ApiProperty } from '@nestjs/swagger';

export class PhotoAddDto {
  @ApiProperty({
    example: 'iVBORw0KGgoAAAANSUhEUgAA...',
    description: 'Conteudo da imagem em base64',
  })
  @IsString()
  @IsNotEmpty()
  imageData!: string;

  @ApiProperty({ example: 'image/jpeg' })
  @IsString()
  @IsNotEmpty()
  mediaType!: string;
}
