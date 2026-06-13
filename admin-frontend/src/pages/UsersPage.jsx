import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

import { Button, Card, Popconfirm, Space, Table, Tag, message } from "antd";
import { DeleteOutlined, EditOutlined, PlusOutlined } from "@ant-design/icons";

import { deleteUser, getUsers } from "../api/adminUsersApi";
import AdminLayout from "../layout/AdminLayout";

export default function UsersPage() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);

  async function loadUsers() {
    setLoading(true);

    try {
      setUsers(await getUsers());
    } catch (error) {
      message.error("Failed to load users.");
    } finally {
      setLoading(false);
    }
  }

  async function handleDelete(id) {
    try {
      await deleteUser(id);
      message.success("User deleted");
      await loadUsers();
    } catch (error) {
      message.error("Failed to delete user.");
    }
  }

  useEffect(() => {
    loadUsers();
  }, []);

  const columns = [
    {
      title: "Username",
      dataIndex: "username",
      key: "username"
    },
    {
      title: "Full name",
      dataIndex: "fullName",
      key: "fullName"
    },
    {
      title: "Email",
      dataIndex: "email",
      key: "email"
    },
    {
      title: "Phone",
      dataIndex: "phoneNumber",
      key: "phoneNumber"
    },
    {
      title: "Roles",
      dataIndex: "roles",
      key: "roles",
      render: (roles = []) => (
        <Space wrap>
          {roles.map((role) => (
            <Tag color={role === "ROLE_TEACHER" ? "blue" : "green"} key={role}>
              {role.replace("ROLE_", "")}
            </Tag>
          ))}
        </Space>
      )
    },
    {
      title: "Actions",
      key: "actions",
      render: (_, user) => (
        <Space>
          <Button icon={<EditOutlined />}>
            <Link to={`/users/${user.id}`}>Edit</Link>
          </Button>
          <Popconfirm
            title="Delete user?"
            description={`Delete ${user.username}?`}
            okText="Delete"
            okButtonProps={{ danger: true }}
            onConfirm={() => handleDelete(user.id)}
          >
            <Button danger icon={<DeleteOutlined />}>
              Delete
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <AdminLayout title="Users" subtitle="Manage student and teacher accounts">
      <Card
        title="Account List"
        extra={
          <Button type="primary" icon={<PlusOutlined />}>
            <Link to="/users/new">New User</Link>
          </Button>
        }
      >
        <Table
          columns={columns}
          dataSource={users}
          loading={loading}
          rowKey="id"
          pagination={{ pageSize: 8 }}
        />
      </Card>
    </AdminLayout>
  );
}
