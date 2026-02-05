
export interface ReportPostRequest {
  reportedPostId: number;
  reason: string;
}

export interface ReportUserRequest {
  reportedUserId: number;
  reason: string;
}

export interface Report {
  id: number;
  reporterId: number;
  reporterUsername: string;
  reportedUserId?: number;
  reportedUsername?: string;
  reportedPostId?: number;
  reportedPostTitle?: string;
  reportType: 'USER' | 'POST';
  reason: string;
  status: 'PENDING' | 'REVIEWED' | 'RESOLVED' | 'DISMISSED';
  adminNotes?: string;
  reviewedBy?: number;
  reviewedByUsername?: string;
  createdAt: string;
  updatedAt: string;
}
