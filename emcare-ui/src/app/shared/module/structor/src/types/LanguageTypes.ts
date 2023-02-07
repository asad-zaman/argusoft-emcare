import { TreeState } from '../store/treeStore/treeStore';

export type Language = {
    code: string;
    display: string;
    localDisplay: string;
};

export enum TranslatableMetadataProperty {
    id = 'id',
    title = 'title',
    description = 'description',
    publisher = 'publisher',
    purpose = 'purpose',
    copyright = 'copyright',
}

export type MetadataProperty = {
    propertyName: TranslatableMetadataProperty;
    label: string;
    markdown: boolean;
    validate?: (value: string, state?: TreeState, targetLanguage?: string) => string;
};

export enum TranslatableItemProperty {
    initial = 'initial',
    text = 'text',
    validationText = 'validationText',
    entryFormatText = 'entryFormatText',
    sublabel = 'sublabel',
    repeatsText = 'repeatsText',
    prefix = 'prefix',
}
