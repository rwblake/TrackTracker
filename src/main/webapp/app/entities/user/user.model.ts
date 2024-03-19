export interface IUser {
  id: number;
  login?: string;
  firstName?: string;
  lastName?: string;
}

export class User implements IUser {
  constructor(public id: number, public login: string) {}
}

export function getUserIdentifier(user: IUser): number {
  return user.id;
}
